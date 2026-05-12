import axios from 'axios'

export interface ApiResponse<T> {
  code: string
  message: string
  data: T
  timestamp: string
}

export const http = axios.create({
  baseURL: '/api',
  timeout: 12000,
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>
    if (payload && typeof payload === 'object' && 'code' in payload) {
      if (payload.code === '200' || payload.code === 'SUCCESS') {
        response.data = payload.data
        return response
      }
      return Promise.reject(new Error(payload.message || '请求失败'))
    }
    return response
  },
  (error) => {
    const message = error?.response?.data?.message || error?.message || '网络异常，请稍后重试'
    return Promise.reject(new Error(message))
  },
)
