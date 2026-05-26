<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Login</h2>
<c:if test="${not empty error}">
  <p>${error}</p>
</c:if>
<form action="${pageContext.request.contextPath}/auth/login" method="post">
  <label>Email</label>
  <input type="email" name="email" required />
  <label>Password</label>
  <input type="password" name="password" required />
  <button type="submit">Login</button>
</form>
<jsp:include page="/components/footer.jsp" />
