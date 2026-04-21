
    async function analyzeText() {
    const textInput = document.getElementById('inputText').value;
    const btn = document.getElementById('analyzeBtn');
    const loading = document.getElementById('loadingIndicator');
    const resultContainer = document.getElementById('resultContainer');
    const barsContainer = document.getElementById('barsContainer');

    if (!textInput.trim()) {
        alert('Bitte geben Sie einen Text ein.');
        return;
    }

    // UI auf "Laden" umstellen
    btn.disabled = true;
    loading.style.display = 'block';
    resultContainer.style.display = 'none';
    barsContainer.innerHTML = '';

    try {
        // Aufruf deines Spring Boot Endpunkts
        const response = await fetch(`/analyze?text=${encodeURIComponent(textInput)}`);
        
        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok');
        }
        
        const data = await response.json();
        
        // UI aktualisieren
        loading.style.display = 'none';
        resultContainer.style.display = 'block';

        // Ergebnisse iterieren und Progress Bars erstellen
        for (const [sentiment, probability] of Object.entries(data)) {
            const percent = (probability * 100).toFixed(2);
            
            // Farben je nach Stimmung wählen
            let colorClass = 'bg-secondary';
            let icon = 'bi-question-circle';
            
            if (sentiment.toLowerCase().includes('positive')) {
                colorClass = 'bg-success';
                icon = 'bi-emoji-smile';
            } else if (sentiment.toLowerCase().includes('negative')) {
                colorClass = 'bg-danger';
                icon = 'bi-emoji-frown';
            } else if (sentiment.toLowerCase().includes('neutral')) {
                colorClass = 'bg-warning text-dark';
                icon = 'bi-emoji-neutral';
            }

            // HTML für den Balken zusammenbauen
            const barHtml = `
                <div class="mb-3">
                    <div class="d-flex justify-content-between mb-1">
                        <span class="fw-medium"><i class="bi ${icon} me-1"></i> ${sentiment}</span>
                        <span class="badge ${colorClass} badge-modern">${percent}%</span>
                    </div>
                    <div class="progress">
                        <div class="progress-bar ${colorClass}" role="progressbar" style="width: ${percent}%" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100"></div>
                    </div>
                </div>
            `;
            barsContainer.innerHTML += barHtml;
        }

    } catch (error) {
        console.error('Fehler:', error);
        alert('Fehler bei der Analyse. Ist das Backend gestartet?');
        loading.style.display = 'none';
    } finally {
        btn.disabled = false;
    }
}