(function () {
  const userId = localStorage.getItem('userId');
  if (!userId) {
    window.location.href = '/html/login.html';
    return;
  }

  document.getElementById('logoutBtn').addEventListener('click', async () => {
    await AuthAPI.logout();
    localStorage.removeItem('userId');
    window.location.href = '/html/login.html';
  });

  const tbody = document.getElementById('expensesTableBody');
  const msgEl = document.getElementById('expenseMsg');
  const form = document.getElementById('expenseForm');
  const categoryInput = document.getElementById('category');
  const amountInput = document.getElementById('amount');
  const expDateInput = document.getElementById('expDate');

  expDateInput.value = new Date().toISOString().slice(0, 10);

  let currentExpenses = [];

  function showMsg(text, isError) {
    msgEl.textContent = text;
    msgEl.className = 'msg ' + (isError ? 'error' : 'success');
  }

  async function loadCategories() {
    try {
      const list = await CategoryAPI.getAll();
      const datalist = document.getElementById('categoryList');
      datalist.innerHTML = '';
      (list || []).forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.name;
        datalist.appendChild(opt);
      });
    } catch (_) {}
  }

  async function fetchExpenses() {
    const year = document.getElementById('filterYear').value ? parseInt(document.getElementById('filterYear').value) : null;
    const month = document.getElementById('filterMonth').value ? parseInt(document.getElementById('filterMonth').value) : null;
    const term = document.getElementById('searchTerm').value?.trim();
    const date = document.getElementById('filterDate').value;

    try {
      if (term) {
        return await ExpenseAPI.searchByCategoryTerm(term);
      }
      if (date) {
        return await ExpenseAPI.getByDate(date);
      }
      if (year && month) {
        return await ExpenseAPI.getByMonth(year, month);
      }
      return await ExpenseAPI.getAll();
    } catch (err) {
      showMsg(err.message || 'Failed to load expenses', true);
      return [];
    }
  }

  function render(expenses) {
    currentExpenses = expenses || [];
    tbody.innerHTML = '';
    if (!currentExpenses.length) {
      tbody.innerHTML = '<tr><td colspan="5" class="text-muted" style="text-align:center;padding:2rem;">No expenses found.</td></tr>';
      return;
    }
    currentExpenses.forEach(e => {
      const tr = document.createElement('tr');
      const catName = e.category?.name ?? e.categoryName ?? 'N/A';
      const date = e.date ?? e.expDate ?? '-';
      tr.innerHTML = `
        <td>${e.id}</td>
        <td>${catName}</td>
        <td>₹${Number(e.amt).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
        <td>${date}</td>
        <td><button class="btn btn-danger deleteBtn" data-id="${e.id}">Delete</button></td>
      `;
      tbody.appendChild(tr);
    });
    tbody.querySelectorAll('.deleteBtn').forEach(btn => {
      btn.addEventListener('click', async () => {
        try {
          await ExpenseAPI.delete(btn.dataset.id);
          showMsg('Deleted', false);
          renderTable();
        } catch (err) {
          showMsg(err.message, true);
        }
      });
    });
  }

  async function renderTable() {
    const expenses = await fetchExpenses();
    render(expenses);
  }

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    try {
      await ExpenseAPI.create({
        categoryName: categoryInput.value.trim(),
        amt: parseFloat(amountInput.value),
        expDate: expDateInput.value
      });
      showMsg('Expense added!', false);
      form.reset();
      expDateInput.value = new Date().toISOString().slice(0, 10);
      renderTable();
      loadCategories();
    } catch (err) {
      showMsg(err.message || 'Failed to add expense', true);
    }
  });

  document.getElementById('btnFilter').addEventListener('click', renderTable);
  document.getElementById('btnClear').addEventListener('click', () => {
    document.getElementById('filterYear').value = '';
    document.getElementById('filterMonth').value = '';
    document.getElementById('searchTerm').value = '';
    document.getElementById('filterDate').value = '';
    renderTable();
  });

  loadCategories();
  renderTable();
})();
