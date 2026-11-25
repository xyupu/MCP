import request from '@/utils/request'

export interface MeResponse {
  userId: number
  username: string
  tenantId: number
  roles: string[]
}

export interface RegisterParams {
  username: string
  password: string
  email: string
  phone: string
}

export interface RegisterResult {
  userId: number
  username: string
  tenantId: number
}

// 注册接口：POST /users
// 阶段0/阶段1 演示：默认租户 1、启用状态 1
export function register(data: RegisterParams) {
  return request.post('/users', {
    username: data.username,
    password: data.password,
    email: data.email,
    phone: data.phone,
    tenantId: 1,
    status: 1
  }) as Promise<RegisterResult>
}

export function getMe() {
  return request.get('/auth/me') as Promise<MeResponse>
}
