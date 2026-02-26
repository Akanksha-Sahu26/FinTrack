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

  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;

  async function load() {
    try {
      const [user, expenses, narrative, summary] = await Promise.all([
        UserAPI.getUserById(userId),
        ExpenseAPI.getByMonth(year, month),
        AIAPI.getFinancialNarrative(year, month),
        ExpenseAPI.getMonthlySummary()
      ]);

      const salary = user?.salary ?? user?.monthlyIncome ?? 0;
      const total = expenses?.reduce((s, e) => s + (e.amt ?? 0), 0) ?? 0;
      const remaining = salary - total;

      document.getElementById('totalSpending').textContent = `₹${Number(total).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
      document.getElementById('remainingSalary').textContent = `₹${Number(remaining).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
      document.getElementById('monthlySummary').textContent = `₹${Number(summary ?? total).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
      document.getElementById('financialNarrative').textContent = narrative?.narrative || 'No data for this month. Add expenses to get insights.';

      // Chart: category breakdown from narrative or expenses
      const breakdown = narrative?.categoryBreakdown || [];
      const labels = breakdown.length ? breakdown.map(b => b.categoryName) : (expenses || []).map(e => e.category?.name || 'Other').filter(Boolean);
      const data = breakdown.length ? breakdown.map(b => b.amount) : [];
      const dataFromExpenses = breakdown.length ? null : (expenses || []).reduce((acc, e) => {
        const n = e.category?.name || 'Other';
        acc[n] = (acc[n] || 0) + Number(e.amt);
        return acc;
      }, {});

      const chartLabels = breakdown.length ? labels : Object.keys(dataFromExpenses || {});
      const chartData = breakdown.length ? data : Object.values(dataFromExpenses || {});

      if (chartLabels.length) {
        const ctx = document.getElementById('categoryChart').getContext('2d');
        const colors = chartLabels.map((_, i) => `hsl(${(i * 137) % 360}, 65%, 60%)`);
        new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels: chartLabels,
            datasets: [{ data: chartData, backgroundColor: colors }]
          },
          options: {
            responsive: true,
            plugins: {
              legend: { position: 'bottom' },
              tooltip: {
                callbacks: { label: ctx => `${ctx.label}: ₹${Number(ctx.raw).toLocaleString('en-IN', { minimumFractionDigits: 2 })}` }
              }
            }
          }
        });
      }
    } catch (err) {
      if (err.message && err.message.includes('401')) {
        localStorage.removeItem('userId');
        window.location.href = '/html/login.html';
        return;
      }
      document.getElementById('financialNarrative').textContent = 'Unable to load. Add expenses for this month first.';
      console.error(err);
    }
  }

  load();
})();
