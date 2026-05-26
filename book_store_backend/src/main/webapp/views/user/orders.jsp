<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Your Orders</h2>
<c:if test="${empty orders}">
  <p>No orders found.</p>
</c:if>
<c:forEach var="order" items="${orders}">
  <div>
    <h4>Order #${order.id} - ${order.status}</h4>
    <p>Total: ${order.totalAmount}</p>
    <ul>
      <c:forEach var="item" items="${order.items}">
        <li>${item.title} x ${item.quantity} - ${item.price}</li>
      </c:forEach>
    </ul>
  </div>
</c:forEach>
<jsp:include page="/components/footer.jsp" />
