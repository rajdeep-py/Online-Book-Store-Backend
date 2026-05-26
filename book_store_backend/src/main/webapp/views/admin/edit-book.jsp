<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Edit Book</h2>
<form action="${pageContext.request.contextPath}/admin/books" method="post">
  <input type="hidden" name="action" value="update" />
  <input type="hidden" name="id" value="${book.id}" />
  <label>Title</label>
  <input type="text" name="title" value="${book.title}" required />
  <label>Author</label>
  <input type="text" name="author" value="${book.author}" required />
  <label>Category</label>
  <input type="text" name="category" value="${book.category}" required />
  <label>Price</label>
  <input type="number" name="price" step="0.01" value="${book.price}" required />
  <label>Stock</label>
  <input type="number" name="stock" min="0" value="${book.stock}" required />
  <label>Image</label>
  <input type="text" name="image" value="${book.image}" />
  <label>Description</label>
  <textarea name="description">${book.description}</textarea>
  <button type="submit">Update</button>
</form>
