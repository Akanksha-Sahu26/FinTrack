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
  document.getElementById('year').value = now.getFullYear();
  document.getElementById('month').value = now.getMonth() + 1;

  let chartInstance = null;

  async function load() {
    const year = parseInt(document.getElementById('year').value) || now.getFullYear();
    const month = parseInt(document.getElementById('month').value) || now.getMonth() + 1;

    try {
      const [budget, narrative] = await Promise.all([
        AIAPI.getBudgetPlanner(year, month),
        AIAPI.getFinancialNarrative(year, month)
      ]);

      document.getElementById('budgetNarrative').textContent =
        budget?.planningNarrative || 'No data for this month. Add expenses to get recommendations.';

      const breakdown = narrative?.categoryBreakdown || [];
      if (breakdown.length && document.getElementById('categoryChart')) {
        if (chartInstance) chartInstance.destroy();
        const ctx = document.getElementById('categoryChart').getContext('2d');
        const labels = breakdown.map(b => b.categoryName);
        const data = breakdown.map(b => Number(b.amount));
        const colors = labels.map((_, i) => `hsl(${(i * 137) % 360}, 65%, 60%)`);
        chartInstance = new Chart(ctx, {
          type: 'doughnut',
          data: {
            labels,
            datasets: [{ data, backgroundColor: colors }]
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
      document.getElementById('budgetNarrative').textContent =
        'Unable to load. Add expenses for this month first.';
      console.error(err);
    }
  }

  document.getElementById('btnLoad').addEventListener('click', load);
  load();
})();
