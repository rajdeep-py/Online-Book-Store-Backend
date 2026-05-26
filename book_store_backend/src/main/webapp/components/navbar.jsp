<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav>
  <a href="${pageContext.request.contextPath}/books">Books</a>
  <a href="${pageContext.request.contextPath}/cart">Cart</a>
  <a href="${pageContext.request.contextPath}/orders">Orders</a>
  <c:choose>
    <c:when test="${not empty sessionScope.userId}">
      <span>Welcome, ${sessionScope.userName}</span>
      <a href="${pageContext.request.contextPath}/auth/logout">Logout</a>
    </c:when>
    <c:otherwise>
      <a href="${pageContext.request.contextPath}/auth/login">Login</a>
      <a href="${pageContext.request.contextPath}/auth/register">Register</a>
    </c:otherwise>
  </c:choose>
</nav>
