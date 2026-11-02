export interface ColumnDto {
  name: string
  type: string | null
}

export interface TableDto {
  name: string
  rowCount: number
  columns: ColumnDto[]
}

export interface SchemaDto {
  name: string
  tables: TableDto[]
}

export interface TreeResponse {
  schemas: SchemaDto[]
}

export interface OpenResponse {
  id: string
  name: string
}

export function getToken(): string {
  return new URLSearchParams(window.location.search).get('token') ?? ''
}

export async function apiFetch(path: string, init?: RequestInit): Promise<Response> {
  const headers = new Headers(init?.headers)
  headers.set('Authorization', `Bearer ${getToken()}`)
  return fetch(path, { ...init, headers })
}

export async function openArchive(path: string): Promise<OpenResponse> {
  const response = await apiFetch('/api/archives', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ path }),
  })
  if (!response.ok) {
    const body = await response.json().catch(() => ({ error: response.statusText }))
    throw new Error(body.error ?? `Request failed: ${response.status}`)
  }
  return response.json()
}

export async function fetchTree(id: string): Promise<TreeResponse> {
  const response = await apiFetch(`/api/archives/${id}/tree`)
  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`)
  }
  return response.json()
}
