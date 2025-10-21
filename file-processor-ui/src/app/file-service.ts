import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './../environments/environment';

export interface FileMetadata {
  id: number;
  fileName: string;
  lineCount: number;
  wordCount: number;
  uploadedAt: string;
}

export interface PagedResponse {
  content: FileMetadata[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class FileService {
  private http = inject(HttpClient);
  private baseUrl = environment.apiUrl;

  upload(file: File): Observable<HttpEvent<unknown>> {
    const formData = new FormData();
    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }


  list(page: number = 0, size: number = 5): Observable<PagedResponse> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse>(`${this.baseUrl}/list`, { params });
  }
}







