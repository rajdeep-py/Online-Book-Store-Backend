<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/navbar.jsp" />
<h2>Register</h2>
<c:if test="${not empty error}">
  <p>${error}</p>
</c:if>
<form action="${pageContext.request.contextPath}/auth/register" method="post">
  <label>Name</label>
  <input type="text" name="name" required />
  <label>Email</label>
  <input type="email" name="email" required />
  <label>Password</label>
  <input type="password" name="password" required />
  <button type="submit">Register</button>
</form>
<jsp:include page="/components/footer.jsp" />
