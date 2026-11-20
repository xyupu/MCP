import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
  userId: number
  username: string
}

export function login(data: LoginParams) {
  return request.post('/auth/login', data) as Promise<LoginResult>
}
