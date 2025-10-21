import { Component, inject, EventEmitter, Output } from '@angular/core';
import { HttpClient } from '@angular/common/http';

interface UploadResponse {
  filename: string;
}

@Component({
  selector: 'app-file-upload',
  imports: [],
  templateUrl: './file-upload.html',
  styleUrls: ['./file-upload.css']
})
export class FileUpload {
  // no need to explicitly type `string` when initialized with ''
  message = '';
  selectedFile: File | null = null;

  // use inject() instead of constructor
  private http = inject(HttpClient);

  @Output() uploadCompleted = new EventEmitter<void>();

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.selectedFile = file;
      this.message = `Selected file: ${file.name}`;
    }
  }

  onUpload() {
    if (!this.selectedFile) {
      this.message = 'No file selected';
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post<UploadResponse>('/api/files/upload', formData).subscribe({
      next: (res) => {
        const filename = (res as any)?.filename ?? this.selectedFile?.name ?? '(unknown)';
        this.message = `✅ Upload successful: ${filename}`;
        console.log('📁 Upload successful:', res);
        this.uploadCompleted.emit();
      },
      error: (err) =>
        (this.message = `Upload failed: ${err.error?.error || err.message}`)
    });
  }
}
