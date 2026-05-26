<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Books</h2>
<form action="${pageContext.request.contextPath}/search" method="get">
  <input type="text" name="q" value="${searchQuery}" placeholder="Search by title or author" />
  <button type="submit">Search</button>
</form>
<c:if test="${not empty error}">
  <p>${error}</p>
</c:if>
<ul>
  <c:forEach var="book" items="${books}">
    <li>
      <strong>${book.title}</strong> by ${book.author} - ${book.price}
      <a href="${pageContext.request.contextPath}/book?id=${book.id}">Details</a>
      <form action="${pageContext.request.contextPath}/cart" method="post">
        <input type="hidden" name="action" value="add" />
        <input type="hidden" name="bookId" value="${book.id}" />
        <input type="number" name="quantity" value="1" min="1" />
        <button type="submit">Add to cart</button>
      </form>
    </li>
  </c:forEach>
</ul>
<jsp:include page="/components/footer.jsp" />
