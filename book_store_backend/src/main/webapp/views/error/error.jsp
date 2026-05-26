<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h2>Something went wrong</h2>
<c:if test="${not empty error}">
  <p>${error}</p>
</c:if>
<a href="${pageContext.request.contextPath}/books">Go to books</a>
