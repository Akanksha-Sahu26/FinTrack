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
      const risk = await AIAPI.getRiskDetection(year, month);

      const risky = risk?.riskyCategories || [];
      const safe = risk?.safeCategories || [];
      const narrative = risk?.riskAnalysisNarrative || '';

      const riskyUl = document.getElementById('riskyList');
      const safeUl = document.getElementById('safeList');

      riskyUl.innerHTML = risky.length ? risky.map(c => `<li>${c}</li>`).join('') : '<li class="text-muted">None</li>';
      safeUl.innerHTML = safe.length ? safe.map(c => `<li>${c}</li>`).join('') : '<li class="text-muted">None</li>';
      document.getElementById('riskNarrative').textContent = narrative || 'No data for this month.';
    } catch (err) {
      if (err.message && err.message.includes('401')) {
        localStorage.removeItem('userId');
        window.location.href = '/html/login.html';
        return;
      }
      document.getElementById('riskyList').innerHTML = '<li class="text-muted">-</li>';
      document.getElementById('safeList').innerHTML = '<li class="text-muted">-</li>';
      document.getElementById('riskNarrative').textContent = 'Unable to load. Add expenses for this month first.';
      console.error(err);
    }
  }

  document.getElementById('btnLoad').addEventListener('click', load);
  load();
})();
