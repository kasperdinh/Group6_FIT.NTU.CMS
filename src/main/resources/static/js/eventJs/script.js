
    document.addEventListener('DOMContentLoaded', function () {
    const table = document.getElementById('eventTable');
    const rows = table.querySelectorAll('.event-row');
    const rowsPerPage = 10;
    const totalRows = rows.length;
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    let currentPage = 1;

    function showPage(page) {
    // Ẩn tất cả các hàng
    rows.forEach(row => row.style.display = 'none');

    // Tính toán chỉ số bắt đầu và kết thúc
    const start = (page - 1) * rowsPerPage;
    const end = Math.min(start + rowsPerPage, totalRows);

    // Hiển thị các hàng trong trang hiện tại
    for (let i = start; i < end; i++) {
    rows[i].style.display = '';
}

    // Cập nhật giao diện phân trang
    updatePagination(page);
}

    function updatePagination(currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';

    // Nút Previous
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
    prevLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage - 1})">Previous</a>`;
    pagination.appendChild(prevLi);

    // Các nút số trang
    for (let i = 1; i <= totalPages; i++) {
    const li = document.createElement('li');
    li.className = `page-item ${i === currentPage ? 'active' : ''}`;
    li.innerHTML = `<a class="page-link" href="#" onclick="changePage(${i})">${i}</a>`;
    pagination.appendChild(li);
}

    // Nút Next
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
    nextLi.innerHTML = `<a class="page-link" href="#" onclick="changePage(${currentPage + 1})">Next</a>`;
    pagination.appendChild(nextLi);
}

    // Hàm thay đổi trang
    window.changePage = function (page) {
    if (page >= 1 && page <= totalPages) {
    currentPage = page;
    showPage(page);
}
};

    // Hiển thị trang đầu tiên khi tải
    if (totalRows > 0) {
    showPage(1);
}
});

