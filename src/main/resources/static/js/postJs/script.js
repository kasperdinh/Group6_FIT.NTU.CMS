
    // Khởi tạo CKEditor và lưu instance
    let editEditor;

    document.addEventListener('DOMContentLoaded', function () {
    // Khởi tạo CKEditor cho modal Edit Post
    ClassicEditor
        .create(document.querySelector('#edit-ckeditor-content'))
        .then(editor => {
            editEditor = editor; // Lưu instance của CKEditor
            console.log('CKEditor initialized for Edit Post');
        })
        .catch(error => {
            console.error('Error initializing CKEditor for Edit Post:', error);
        });

    // Xử lý sự kiện click nút Edit
    document.querySelectorAll('.edit-btn').forEach(button => {
    button.addEventListener('click', function () {
    const postId = this.getAttribute('data-post-id');
    fetch(`/posts/${postId}`)
    .then(response => {
    if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
}
    return response.json();
})
    .then(post => {
    // Điền các trường vào modal editPostModal
    document.getElementById('editPostId').value = post.postId || '';
    document.getElementById('editPostTitle').value = post.postTitle || '';
    document.getElementById('editSummary').value = post.summary || '';

    // Gán giá trị cho CKEditor (content)
    if (editEditor) {
    editEditor.setData(post.content || ''); // Sử dụng setData() của CKEditor
} else {
    console.warn('CKEditor is not initialized yet. Falling back to textarea.');
    document.getElementById('edit-ckeditor-content').value = post.content || '';
}

    // Format dates for datetime-local input
    const formatDateForInput = (date) => {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().slice(0, 16); // Định dạng YYYY-MM-DDThh:mm
};
    document.getElementById('editCreationDate').value = formatDateForInput(post.creationDate);
    document.getElementById('editUpdateDate').value = formatDateForInput(post.updateDate);

    // Gán giá trị cho các trường khác
    document.getElementById('editPostImage').src = post.postImage || '';
    document.getElementById('editPostImage').style.display = post.postImage ? 'block' : 'none';
    document.getElementById('editExistingImage').value = post.postImage || '';
    document.getElementById('editFilePathLink').href = post.filePath || '';
    document.getElementById('editFilePathLink').textContent = post.filePath || 'No file';
    document.getElementById('editFilePathLink').style.display = post.filePath ? 'block' : 'none';
    document.getElementById('editExistingFilePath').value = post.filePath || '';
    document.getElementById('editStatus').value = post.status || 'Pending';

    // Gán giá trị cho category và page
    const editCategory = document.getElementById('editCategory');
    const editPage = document.getElementById('editPage');
    if (editCategory && editPage) {
    editCategory.value = post.category ? post.category.tittleId : '';
    editPage.value = post.page ? post.page.pageId : '';
}
})
    .catch(error => console.error('Error fetching post:', error));
});
});

    // Xử lý sự kiện click nút Delete
    document.querySelectorAll('.delete-btn').forEach(button => {
    button.addEventListener('click', function () {
    const postId = this.getAttribute('data-post-id');
    document.getElementById('deletePostId').value = postId;
});
});

    // Xử lý checkbox Select All
    const selectAllCheckbox = document.getElementById('selectAll');
    const postCheckboxes = document.querySelectorAll('input[name="postIds"]');
    if (selectAllCheckbox) {
    selectAllCheckbox.addEventListener('change', function () {
    postCheckboxes.forEach(checkbox => {
    checkbox.checked = this.checked;
});
});
}
});
