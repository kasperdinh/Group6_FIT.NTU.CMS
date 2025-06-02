document.addEventListener('DOMContentLoaded', function () {
        const selectAllCheckbox = document.getElementById('selectAll');
        const postCheckboxes = document.querySelectorAll('input[name="postIds"]');

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
                        document.getElementById('editContent').value = post.content || '';
                        // Format dates for datetime-local input
                        document.getElementById('editCreationDate').value = post.creationDate ? new Date(post.creationDate).toISOString().slice(0, 16) : '';
                        document.getElementById('editUpdateDate').value = post.updateDate ? new Date(post.updateDate).toISOString().slice(0, 16) : '';
                        document.getElementById('editPostImage').src = post.postImage || '';
                        document.getElementById('editPostImage').style.display = post.postImage ? 'block' : 'none';
                        document.getElementById('editExistingImage').value = post.postImage || '';
                        document.getElementById('editFilePathLink').href = post.filePath || '';
                        document.getElementById('editFilePathLink').textContent = post.filePath || 'No file';
                        document.getElementById('editFilePathLink').style.display = post.filePath ? 'block' : 'none';
                        document.getElementById('editExistingFilePath').value = post.filePath || '';
                        document.getElementById('editStatus').value = post.status || 'Pending';
                        // Giả sử category là một trường select, bạn cần điền giá trị
                        const editCategory = document.getElementById('editCategory');
                        const editPage=document.getElementById('editPage');
                        if (editCategory && editPage) {
                            editCategory.value = post.category ? post.category.tittleId : '';
                            editPage.value = post.page ? post.page.pageId : '';
                        }
                    })
                    .catch(error => console.error('Error fetching post:', error));
            });
        });

         document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function () {
            const postId = this.getAttribute('data-post-id');
            document.getElementById('deletePostId').value = postId;
        });
    });

        selectAllCheckbox.addEventListener('change', function () {
            postCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });
});