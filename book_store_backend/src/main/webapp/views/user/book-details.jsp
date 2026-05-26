<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<c:if test="${empty book}">
  <p>Book not found.</p>
</c:if>
<c:if test="${not empty book}">
  <h2>${book.title}</h2>
  <p>Author: ${book.author}</p>
  <p>Category: ${book.category}</p>
  <p>Price: ${book.price}</p>
  <p>${book.description}</p>
  <form action="${pageContext.request.contextPath}/cart" method="post">
    <input type="hidden" name="action" value="add" />
    <input type="hidden" name="bookId" value="${book.id}" />
    <input type="number" name="quantity" value="1" min="1" />
    <button type="submit">Add to cart</button>
  </form>
</c:if>
<jsp:include page="/components/footer.jsp" />
