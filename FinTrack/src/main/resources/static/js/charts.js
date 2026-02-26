// charts.js

// Utility: generate random pastel colors for charts
function getRandomColors(n) {
    const colors = [];
    for (let i = 0; i < n; i++) {
        const hue = Math.floor(Math.random() * 360);
        colors.push(`hsl(${hue}, 70%, 70%)`);
    }
    return colors;
}

// Render Category Expenses Pie Chart (Dashboard)
function renderCategoryChart(expenses, canvasId = 'categoryChart') {
    const ctx = document.getElementById(canvasId).getContext('2d');

    const categoryTotals = {};
    expenses.forEach(e => {
        categoryTotals[e.categoryName] = (categoryTotals[e.categoryName] || 0) + e.amt;
    });

    const labels = Object.keys(categoryTotals);
    const data = Object.values(categoryTotals);
    const bgColors = getRandomColors(labels.length);

    new Chart(ctx, {
        type: 'pie',
        data: {
            labels,
            datasets: [{
                data,
                backgroundColor: bgColors,
                borderColor: '#fff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        boxWidth: 20,
                        padding: 15
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            return `${context.label}: ₹${context.raw.toFixed(2)}`;
                        }
                    }
                }
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    });
}

// Render Budget Planner Doughnut Chart
function renderBudgetChart(recommendations, canvasId = 'budgetChart') {
    const ctx = document.getElementById(canvasId).getContext('2d');

    const labels = recommendations.map(r => r.categoryName);
    const data = recommendations.map(r => r.recommendedAmount);
    const bgColors = getRandomColors(labels.length);

    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels,
            datasets: [{
                data,
                backgroundColor: bgColors,
                borderColor: '#fff',
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { boxWidth: 20, padding: 15 }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const percentage = ((context.raw / data.reduce((a,b)=>a+b,0))*100).toFixed(1);
                            return `${context.label}: ₹${context.raw.toFixed(2)} (${percentage}%)`;
                        }
                    }
                }
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    });
}

// Render AI Budget vs Current Expenses Comparison (Optional)
function renderComparisonChart(currentExpenses, recommended, canvasId = 'comparisonChart') {
    const ctx = document.getElementById(canvasId).getContext('2d');

    const categories = Array.from(new Set([
        ...currentExpenses.map(e => e.categoryName),
        ...recommended.map(r => r.categoryName)
    ]));

    const currentData = categories.map(cat => {
        const found = currentExpenses.find(e => e.categoryName === cat);
        return found ? found.amt : 0;
    });

    const recommendedData = categories.map(cat => {
        const found = recommended.find(r => r.categoryName === cat);
        return found ? r.recommendedAmount : 0;
    });

    const bgColors = getRandomColors(categories.length);

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: categories,
            datasets: [
                { label: 'Current Spending', data: currentData, backgroundColor: 'rgba(255,99,132,0.6)' },
                { label: 'Recommended', data: recommendedData, backgroundColor: 'rgba(54,162,235,0.6)' }
            ]
        },
        options: {
            responsive: true,
            plugins: { legend: { position: 'bottom' } },
            animation: { duration: 1000, easing: 'easeOutQuart' },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}