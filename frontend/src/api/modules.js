import request from './request'

export const authApi = {
  login: (payload) => request.post('/auth/login', payload),
  register: (payload) => request.post('/auth/register', payload),
  me: () => request.get('/auth/me'),
  updateProfile: (payload) => request.put('/account/profile', payload)
}

export const routeApi = {
  list: (params) => request.get('/routes', { params }),
  detail: (id) => request.get(`/routes/${id}`)
}

export const orderApi = {
  list: (params) => request.get('/orders', { params }),
  detail: (orderNo) => request.get(`/orders/${orderNo}`),
  create: (payload) => request.post('/orders', payload),
  pay: (orderNo) => request.post(`/orders/${orderNo}/pay`),
  cancel: (orderNo) => request.post(`/orders/${orderNo}/cancel`),
  refund: (orderNo, payload) => request.post(`/orders/${orderNo}/refunds`, payload),
  review: (orderNo, payload) => request.post(`/orders/${orderNo}/reviews`, payload)
}

export const accountApi = {
  travelers: () => request.get('/travelers'),
  createTraveler: (payload) => request.post('/travelers', payload),
  updateTraveler: (id, payload) => request.put(`/travelers/${id}`, payload),
  deleteTraveler: (id) => request.delete(`/travelers/${id}`),
  favorites: () => request.get('/favorites'),
  addFavorite: (routeId) => request.post('/favorites', { routeId }),
  removeFavorite: (routeId) => request.delete(`/favorites/${routeId}`),
  messages: () => request.get('/messages'),
  unreadCount: () => request.get('/messages/unread-count'),
  readMessage: (id) => request.post(`/messages/${id}/read`),
  consultations: () => request.get('/consultations'),
  createConsultation: (payload) => request.post('/consultations', payload)
}

export const contentApi = {
  articles: () => request.get('/articles'),
  article: (id) => request.get(`/articles/${id}`),
  attractions: (params) => request.get('/attractions', { params })
}

export const adminApi = {
  dashboard: () => request.get('/admin/dashboard'),
  routes: (params) => request.get('/admin/routes', { params }),
  route: (id) => request.get(`/admin/routes/${id}`),
  createRoute: (payload) => request.post('/admin/routes', payload),
  updateRouteStatus: (id, status) => request.patch(`/admin/routes/${id}/status`, { status }),
  departures: (params) => request.get('/admin/departures', { params }),
  createDeparture: (payload) => request.post('/admin/departures', payload),
  updateDepartureStatus: (id, status) => request.patch(`/admin/departures/${id}/status`, { status }),
  orders: (params) => request.get('/admin/orders', { params }),
  confirmOrder: (orderNo) => request.post(`/admin/orders/${orderNo}/confirm`),
  refunds: (params) => request.get('/admin/refunds', { params }),
  refundDecision: (id, payload) => request.post(`/admin/refunds/${id}/decision`, payload),
  attractions: () => request.get('/admin/attractions'),
  hotels: () => request.get('/admin/hotels'),
  guides: () => request.get('/admin/guides'),
  users: (params) => request.get('/admin/users', { params }),
  staff: (params) => request.get('/admin/staff', { params }),
  logs: () => request.get('/admin/logs')
}

export const guideApi = {
  dashboard: () => request.get('/guide/dashboard'),
  departures: (params) => request.get('/guide/departures', { params }),
  detail: (id) => request.get(`/guide/departures/${id}`),
  passengers: (id) => request.get(`/guide/departures/${id}/passengers`),
  status: (id, status) => request.patch(`/guide/departures/${id}/status`, { status })
}
