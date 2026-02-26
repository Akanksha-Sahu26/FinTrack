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

  const msgEl = document.getElementById('categoryMsg');
  const listEl = document.getElementById('categoryList');

  function showMsg(text, isError) {
    msgEl.textContent = text;
    msgEl.className = 'msg ' + (isError ? 'error' : 'success');
  }

  async function loadCategories(keyword) {
    try {
      const list = keyword ? await CategoryAPI.search(keyword) : await CategoryAPI.getAll();
      listEl.innerHTML = '';
      (list || []).forEach(c => {
        const chip = document.createElement('span');
        chip.className = 'category-chip';
        chip.innerHTML = `<span>${c.name}</span>`;
        listEl.appendChild(chip);
      });
    } catch (err) {
      if (err.message && err.message.includes('401')) {
        localStorage.removeItem('userId');
        window.location.href = '/html/login.html';
        return;
      }
      listEl.innerHTML = '<span class="text-muted">Failed to load categories.</span>';
    }
  }

  document.getElementById('btnAdd').addEventListener('click', async () => {
    const name = document.getElementById('newCategory').value?.trim();
    if (!name) {
      showMsg('Enter a category name', true);
      return;
    }
    try {
      await CategoryAPI.create(name);
      showMsg('Category added!', false);
      document.getElementById('newCategory').value = '';
      loadCategories();
    } catch (err) {
      showMsg(err.message || 'Failed to add category', true);
    }
  });

  document.getElementById('searchCategory').addEventListener('input', (e) => {
    const v = e.target.value?.trim();
    loadCategories(v || undefined);
  });

  loadCategories();
})();
