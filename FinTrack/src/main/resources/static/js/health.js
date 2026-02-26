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

  async function load() {
    const year = parseInt(document.getElementById('year').value) || now.getFullYear();
    const month = parseInt(document.getElementById('month').value) || now.getMonth() + 1;

    try {
      const health = await AIAPI.getHealthScore(year, month);
      document.getElementById('healthScore').textContent = health?.healthScore ?? 0;
      document.getElementById('healthLevel').textContent = health?.healthLevel || '-';
      document.getElementById('healthExplanation').textContent =
        health?.explanation || 'No data for this month.';
    } catch (err) {
      if (err.message && err.message.includes('401')) {
        localStorage.removeItem('userId');
        window.location.href = '/html/login.html';
        return;
      }
      document.getElementById('healthScore').textContent = '-';
      document.getElementById('healthLevel').textContent = '-';
      document.getElementById('healthExplanation').textContent =
        'Unable to load. Add expenses for this month first.';
      console.error(err);
    }
  }

  document.getElementById('btnLoad').addEventListener('click', load);
  load();
})();
