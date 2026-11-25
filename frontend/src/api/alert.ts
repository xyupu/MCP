import request from '@/utils/request'

export interface Alert {
  id: number
  fingerprint: string
  alertName: string
  severity: string
  status: string
  source: string
  namespace: string
  service: string
  instance: string
  startsAt: string
  endsAt: string
}

export function listAlerts(params?: { status?: string; severity?: string }) {
  return request.get('/alerts', { params }) as Promise<Alert[]>
}

export function getAlert(id: number) {
  return request.get(`/alerts/${id}`) as Promise<Alert>
}
