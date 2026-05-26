<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Your Cart</h2>
<c:if test="${empty cartItems}">
  <p>Your cart is empty.</p>
</c:if>
<c:if test="${not empty cartItems}">
  <table>
    <tr>
      <th>Book</th>
      <th>Quantity</th>
      <th>Price</th>
      <th>Action</th>
    </tr>
    <c:forEach var="item" items="${cartItems}">
      <tr>
        <td>${item.title}</td>
        <td>
          <form action="${pageContext.request.contextPath}/cart" method="post">
            <input type="hidden" name="action" value="update" />
            <input type="hidden" name="itemId" value="${item.id}" />
            <input type="number" name="quantity" value="${item.quantity}" min="1" />
            <button type="submit">Update</button>
          </form>
        </td>
        <td>${item.price}</td>
        <td>
          <form action="${pageContext.request.contextPath}/cart" method="post">
            <input type="hidden" name="action" value="remove" />
            <input type="hidden" name="itemId" value="${item.id}" />
            <button type="submit">Remove</button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
  <p>Total: ${cartTotal}</p>
  <a href="${pageContext.request.contextPath}/checkout">Checkout</a>
</c:if>
<jsp:include page="/components/footer.jsp" />
