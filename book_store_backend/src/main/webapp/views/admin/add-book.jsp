<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Add Book</h2>
<form action="${pageContext.request.contextPath}/admin/books" method="post">
  <input type="hidden" name="action" value="create" />
  <label>Title</label>
  <input type="text" name="title" required />
  <label>Author</label>
  <input type="text" name="author" required />
  <label>Category</label>
  <input type="text" name="category" required />
  <label>Price</label>
  <input type="number" name="price" step="0.01" required />
  <label>Stock</label>
  <input type="number" name="stock" min="0" required />
  <label>Image</label>
  <input type="text" name="image" />
  <label>Description</label>
  <textarea name="description"></textarea>
  <button type="submit">Save</button>
</form>
