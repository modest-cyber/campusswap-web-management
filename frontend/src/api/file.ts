import { request } from './http'

export interface UploadResponse {
  url: string
  filename: string
}

/**
 * 上传文件
 * @param file 文件对象
 * @returns 上传结果
 */
export function uploadFile(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request<UploadResponse>({
    method: 'POST',
    url: '/api/file/upload',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
