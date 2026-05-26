<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Manage Orders</h2>
<table>
  <tr>
    <th>Order</th>
    <th>User</th>
    <th>Total</th>
    <th>Status</th>
    <th>Action</th>
  </tr>
  <c:forEach var="order" items="${orders}">
    <tr>
      <td>#${order.id}</td>
      <td>${order.userId}</td>
      <td>${order.totalAmount}</td>
      <td>${order.status}</td>
      <td>
        <form action="${pageContext.request.contextPath}/admin/orders" method="post">
          <input type="hidden" name="orderId" value="${order.id}" />
          <select name="status">
            <option value="PLACED">PLACED</option>
            <option value="SHIPPED">SHIPPED</option>
            <option value="DELIVERED">DELIVERED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
          <button type="submit">Update</button>
        </form>
      </td>
    </tr>
  </c:forEach>
</table>
