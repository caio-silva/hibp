    // Function to fetch data from the URL
async function fetchData(url) {
  const response = await fetch(url);
  const textData = await response.text();
  return textData;
}

// Function to parse and process the data
function processData(textData) {
  const entries = textData.split('\n');

  const data = entries.map(entry => {
    const [timestamp, thread, level, className, message] = entry.split(',');
    return {
      timestamp: timestamp.trim(),
      thread: thread.trim(),
      level: level.trim(),
      className: className.trim(),
      message: message.trim()
    };
  });

  return data;
}

// Function to count the frequency of calls
function countCallFrequency(data) {
  const callCounts = {};
  data.forEach(entry => {
    const className = entry.className;
    callCounts[className] = callCounts[className] ? callCounts[className] + 1 : 1;
  });
  return callCounts;
}

// Function to count the success and error calls
function countSuccessError(data) {
  let successCount = 0;
  let errorCount = 0;
  data.forEach(entry => {
    const level = entry.level.toLowerCase();
    const message = entry.message.toLowerCase();
    if (level === 'success' || message.includes('call was successful')) {
      successCount++;
    } else if (level === 'error') {
      errorCount++;
    }
  });
  return [successCount, errorCount];
}

// Function to create the call frequency chart
function createCallFrequencyChart(labels, frequencies) {
  labels = ["Error", "Success", "Total"];
  frequencies = [...frequencies, frequencies[0] + frequencies[1]]
  const callFrequencyCanvas = document.createElement('canvas');
  callFrequencyCanvas.id = 'chart-call-frequency';
  document.body.appendChild(callFrequencyCanvas);

  new Chart(callFrequencyCanvas, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Call Totals',
        data: frequencies,
        backgroundColor: 'rgba(75, 192, 192, 0.8)',
        borderWidth: 1
      }]
    },
    options: {
      scales: {
        y: {
          beginAtZero: true,
          stepSize: 1
        }
      },
      responsive: false, // Disable automatic resizing
      maintainAspectRatio: false
    }
  });
}

// Function to create the success/error percentage chart
function createSuccessErrorChart(labels, data) {
  const successErrorCanvas = document.createElement('canvas');
  successErrorCanvas.id = 'chart-success-error-percentage';
  document.body.appendChild(successErrorCanvas);

  const total = data[0] + data[1]
  data[0] = (data[0]* 100.0)/total
  data[1] = (data[1]* 100.0)/total

  new Chart(successErrorCanvas, {
    type: 'pie',
    data: {
      labels: labels,
      datasets: [{
        label: 'Success/Error Percentage',
        data: data,
        backgroundColor: ['rgba(75, 192, 192, 0.8)', 'rgba(255, 99, 132, 0.8)'],
        borderWidth: 1
      }]
    },
    options: {
//      responsive: true
      responsive: false, // Disable automatic resizing
      maintainAspectRatio: false
    }
  });
}
//////////////////////////

function createScatterPlot(data) {
  const levelValues = {
    WARN: 3,
    INFO: 2,
    ERROR: 1
    // Add more levels and their corresponding values if needed
  };

  const scatterData = data.map(entry => ({
    x: entry.timestamp,
    y: levelValues[entry.level] || 0
  }));

  const scatterChartCanvas = document.createElement('canvas');
  scatterChartCanvas.id = 'chart-scatter';
  document.body.appendChild(scatterChartCanvas);

  new Chart(scatterChartCanvas, {
    type: 'scatter',
    data: {
      datasets: [{
        label: 'Scatter Plot',
        data: scatterData,
        backgroundColor: 'rgba(75, 192, 192, 0.8)',
        borderColor: 'rgba(75, 192, 192, 1)',
        hoverBackgroundColor: 'rgba(75, 192, 192, 0.4)',
        hoverBorderColor: 'rgba(75, 192, 192, 1)',
        pointRadius: 5,
        pointHoverRadius: 7
      }]
    },
    options: {
      scales: {
        x: {
          title: {
            display: true,
            text: 'Timestamp'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Level'
          },
          ticks: {
            callback: function(value, index, values) {
              const levelNames = Object.keys(levelValues);
              if (value >= 0 && value < levelNames.length) {
                return levelNames[value];
              }
              return value;
            },
            stepSize: 1
          }
        }
      },
      responsive: false, // Disable automatic resizing
      maintainAspectRatio: false,
      tooltips: {
        callbacks: {
          label: function (tooltipItem, data) {
            const point = data.datasets[tooltipItem.datasetIndex].data[tooltipItem.index];
            const levelName = Object.keys(levelValues)[point.y];
            return `Timestamp: ${point.x}\nLevel: ${levelName}`;
          }
        }
      }
    }
  });
}

//////////////////////////
// Main async function
async function main() {
  try {
    const url = 'http://192.168.0.3:7676/logs';
//    const url = 'http://localhost:7777/logs';

    // Fetch data
    const textData = await fetchData(url);

    // Process data
    const data = processData(textData);

    // Count call frequency
    const callCounts = countCallFrequency(data);
    const callLabels = Object.keys(callCounts);
    const callFrequencies = Object.values(callCounts);

    // Count success/error calls
    const [successCount, errorCount] = countSuccessError(data);
    const successErrorLabels = ['Success', 'Error'];
    const successErrorData = [successCount, errorCount];

    // Create the call frequency chart
    createCallFrequencyChart(callLabels, callFrequencies);

    // Create the success/error percentage chart
    createSuccessErrorChart(successErrorLabels, successErrorData);

    // Add more chart creation functions here...
    createScatterPlot(data)

  } catch (error) {
    console.error('Error fetching or processing data:', error);
  }
}

// Call the main function
main();