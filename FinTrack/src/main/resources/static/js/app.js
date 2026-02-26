const Dashboard = {
    load: async () => {
        try {
            const userId = localStorage.getItem("userId");
            const user = await UserAPI.getUserById(userId);
            const expenses = await ExpenseAPI.getAll();
            const narrative = await AIAPI.getFinancialNarrative();

            // total expenses
            const total = expenses.reduce((sum, e) => sum + e.amt, 0);
            document.getElementById("totalExpenses").textContent = `₹${total.toFixed(2)}`;

            // remaining salary
            const remaining = user.salary - total;
            document.getElementById("remainingSalary").textContent = `₹${remaining.toFixed(2)}`;

            // AI narrative
            document.getElementById("financialNarrative").textContent = narrative.planningNarrative || "No narrative available.";

            // category chart
            const ctx = document.getElementById("categoryChart").getContext("2d");
            const categories = {};
            expenses.forEach(e => categories[e.categoryName] = (categories[e.categoryName] || 0) + e.amt);
            new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: Object.keys(categories),
                    datasets: [{data: Object.values(categories), backgroundColor: ['#FF6384','#36A2EB','#FFCE56','#4BC0C0','#9966FF']}]
                }
            });

        } catch (err) {
            console.error(err);
        }
    }
};

const ExpensesPage = {
    init: async () => {
        const form = document.getElementById("expenseForm");
        const tableBody = document.querySelector("#expensesTable tbody");

        const renderTable = async () => {
            const expenses = await ExpenseAPI.getAll();
            tableBody.innerHTML = "";
            expenses.forEach(e => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${e.id}</td>
                    <td>${e.categoryName}</td>
                    <td>₹${e.amt}</td>
                    <td>${e.expDate}</td>
                    <td><button class="deleteBtn" data-id="${e.id}">Delete</button></td>
                `;
                tableBody.appendChild(tr);
            });

            document.querySelectorAll(".deleteBtn").forEach(btn => {
                btn.addEventListener("click", async () => {
                    await ExpenseAPI.delete(btn.dataset.id);
                    renderTable();
                });
            });
        };

        form.addEventListener("submit", async (e) => {
            e.preventDefault();
            const data = {
                categoryName: document.getElementById("category").value,
                amt: parseFloat(document.getElementById("amount").value),
                expDate: document.getElementById("expDate").value
            };
            await ExpenseAPI.create(data);
            form.reset();
            renderTable();
        });

        renderTable();
    }
};

const BudgetPlannerPage = {
    load: async () => {
        const budget = await AIAPI.getBudgetPlanner();
        document.getElementById("budgetNarrative").textContent = budget.planningNarrative;

        const ctx = document.getElementById("budgetChart").getContext("2d");
        const labels = budget.recommendedCategoryAllocations.map(a => a.categoryName);
        const data = budget.recommendedCategoryAllocations.map(a => a.recommendedAmount);
        new Chart(ctx, { type: 'doughnut', data: {labels, datasets:[{data, backgroundColor: ['#FF6384','#36A2EB','#FFCE56','#4BC0C0','#9966FF']}] } });
    }
};

const HealthPage = {
    load: async () => {
        const health = await AIAPI.getFinancialHealth();
        document.getElementById("healthScore").textContent = health.healthScore;
        document.getElementById("healthLevel").textContent = health.healthLevel;
        document.getElementById("healthExplanation").textContent = health.explanation;
    }
};
