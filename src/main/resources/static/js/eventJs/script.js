
    let editEditor;

    document.addEventListener('DOMContentLoaded', function () {
    // Initialize CKEditor for Add Event Modal
    ClassicEditor
        .create(document.querySelector('#ckeditor-content'))
        .then(editor => {
            console.log('CKEditor initialized for Add Event');
        })
        .catch(error => {
            console.error('CKEditor Add Error:', error);
        });

    // Initialize CKEditor for Edit Event Modal
    ClassicEditor
    .create(document.querySelector('#editEventDescription'))
    .then(editor => {
    editEditor = editor;
    console.log('CKEditor initialized for Edit Event');
})
    .catch(error => {
    console.error('CKEditor Edit Error:', error);
});

    // Pagination
    const cards = document.querySelectorAll('.event-card');
    const rowsPerPage = 6;
    const totalRows = cards.length;
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    let currentPage = 1;

    function showPage(page) {
    cards.forEach(card => card.style.display = 'none');
    const start = (page - 1) * rowsPerPage;
    const end = Math.min(start + rowsPerPage, totalRows);
    for (let i = start; i < end; i++) {
    cards[i].style.display = 'block';
}
    updatePagination(page);
}

    function updatePagination(currentPage) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;
    pagination.innerHTML = '';

    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Previous</a>`;
    pagination.appendChild(prevLi);

    for (let i = 1; i <= totalPages; i++) {
    const li = document.createElement('li');
    li.className = `page-item ${i === currentPage ? 'active' : ''}`;
    li.innerHTML = `<a class="page-link" href="#" onclick="changePage(${i})">${i}</a>`;
    pagination.appendChild(li);
}

    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Next</a>`;
    pagination.appendChild(nextLi);
}

    window.changePage = function (page) {
    if (page >= 1 && page <= totalPages) {
    currentPage = page;
    showPage(page);
}
};

    if (totalRows > 0) {
    showPage(1);
}

    // Delete Button
    document.querySelectorAll('.delete-btn').forEach(button => {
    button.addEventListener('click', function () {
    const eventId = this.getAttribute('data-event-id');
    document.getElementById('deleteEventId').value = eventId;
});
});

    // Edit Button
    document.querySelectorAll('.edit-btn').forEach(button => {
    button.addEventListener('click', function () {
    const eventId = this.getAttribute('data-event-id');
    fetch(`/api/events/${eventId}`)
    .then(response => {
    if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
}
    return response.json();
})
    .then(event => {
    document.getElementById('editEventId').value = event.eventId || '';
    document.getElementById('editEventName').value = event.eventName || '';
    document.getElementById('editSummary').value = event.summary || '';
    document.getElementById('editEventLocation').value = event.eventLocation || '';
    if (editEditor) {
    editEditor.setData(event.eventDescription || '');
} else {
    console.warn('CKEditor is not initialized yet. Falling back to textarea.');
    document.getElementById('editEventDescription').value = event.eventDescription || '';
}
    const formatDateForInput = (date) => {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().slice(0, 16);
};
    document.getElementById('editBeginDate').value = formatDateForInput(event.beginDate);
    document.getElementById('editFinishDate').value = formatDateForInput(event.finishDate);
    document.getElementById('editEventImage').src = event.eventImage || '';
    document.getElementById('editEventImage').style.display = event.eventImage ? 'block' : 'none';
    document.getElementById('editFilePathLink').href = event.filePath || '';
    document.getElementById('editFilePathLink').textContent = event.filePath || 'Không có tệp';
    document.getElementById('editFilePathLink').style.display = event.filePath ? 'block' : 'none';
    document.getElementById('editExistingImage').value = event.eventImage || '';
    document.getElementById('editExistingFilePath').value = event.filePath || '';
})
    .catch(error => {
    console.error('Error fetching event:', error);
    alert('Không thể tải dữ liệu sự kiện. Vui lòng thử lại.');
});
});
});
});
