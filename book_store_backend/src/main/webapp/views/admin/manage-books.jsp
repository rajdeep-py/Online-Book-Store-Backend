<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Manage Books</h2>
<a href="${pageContext.request.contextPath}/admin/books?action=add">Add Book</a>
<table>
  <tr>
    <th>Title</th>
    <th>Author</th>
    <th>Price</th>
    <th>Stock</th>
    <th>Action</th>
  </tr>
  <c:forEach var="book" items="${books}">
    <tr>
      <td>${book.title}</td>
      <td>${book.author}</td>
      <td>${book.price}</td>
      <td>${book.stock}</td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/books?action=edit&id=${book.id}">Edit</a>
        <form action="${pageContext.request.contextPath}/admin/books" method="post">
          <input type="hidden" name="action" value="delete" />
          <input type="hidden" name="id" value="${book.id}" />
          <button type="submit">Delete</button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>
