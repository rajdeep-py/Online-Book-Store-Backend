<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Customers</h2>
<table>
  <tr>
    <th>Name</th>
    <th>Email</th>
    <th>Role</th>
  </tr>
  <c:forEach var="user" items="${users}">
    <tr>
      <td>${user.name}</td>
      <td>${user.email}</td>
      <td>${user.role}</td>
    </tr>
  </c:forEach>
</table>
