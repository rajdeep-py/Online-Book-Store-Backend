<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Checkout</h2>
<c:if test="${empty cartItems}">
  <p>Your cart is empty.</p>
</c:if>
<c:if test="${not empty cartItems}">
  <ul>
    <c:forEach var="item" items="${cartItems}">
      <li>${item.title} x ${item.quantity} - ${item.price}</li>
    </c:forEach>
  </ul>
  <p>Total: ${cartTotal}</p>
  <form action="${pageContext.request.contextPath}/checkout" method="post">
    <button type="submit">Place Order</button>
  </form>
</c:if>
<jsp:include page="/components/footer.jsp" />
