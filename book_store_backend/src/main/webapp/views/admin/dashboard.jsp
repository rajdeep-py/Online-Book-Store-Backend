<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/components/sidebar.jsp" />
<h2>Admin Dashboard</h2>
<c:if test="${not empty analytics}">
  <p>Users: ${analytics.userCount}</p>
  <p>Books: ${analytics.bookCount}</p>
  <p>Orders: ${analytics.orderCount}</p>
  <p>Revenue: ${analytics.revenue}</p>
</c:if>
