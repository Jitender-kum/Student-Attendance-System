import { API } from '../config/api'
import { apiRequest } from './http'

export const authService = {
  signup(payload) {
    return apiRequest(API.auth.signup, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
  login(payload) {
    return apiRequest(API.auth.login, {
      method: 'POST',
      body: JSON.stringify(payload),
    })
  },
}
