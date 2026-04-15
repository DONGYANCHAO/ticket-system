const TOKEN_KEY = 'token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getRefreshToken() {
  return localStorage.getItem('refreshToken') || ''
}

export function setRefreshToken(token) {
  localStorage.setItem('refreshToken', token)
}

export function removeRefreshToken() {
  localStorage.removeItem('refreshToken')
}
