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

  const msgEl = document.getElementById('profileMsg');
  function showMsg(text, isError) {
    msgEl.textContent = text;
    msgEl.className = 'msg ' + (isError ? 'error' : 'success');
  }

  async function loadUser() {
    try {
      const user = await UserAPI.getUserById(userId);
      document.getElementById('name').value = user?.name || '';
      document.getElementById('email').value = user?.email || '';
      document.getElementById('salary').value = user?.salary ?? '';
    } catch (err) {
      if (err.message && err.message.includes('401')) {
        localStorage.removeItem('userId');
        window.location.href = '/html/login.html';
        return;
      }
      showMsg('Failed to load profile', true);
    }
  }

  document.getElementById('profileForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    try {
      const data = { name, email };
      if (password) data.password = password;
      await UserAPI.updateUser(userId, data);
      showMsg('Profile updated!', false);
    } catch (err) {
      showMsg(err.message || 'Update failed', true);
    }
  });

  document.getElementById('incomeForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const salary = parseFloat(document.getElementById('salary').value);
    try {
      await UserAPI.updateIncome(userId, salary);
      showMsg('Income updated!', false);
    } catch (err) {
      showMsg(err.message || 'Update failed', true);
    }
  });

  loadUser();
})();
