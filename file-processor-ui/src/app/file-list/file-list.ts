import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FileService, FileMetadata } from './../file-service';

@Component({
  selector: 'app-file-list',
  imports: [CommonModule, DatePipe],
  templateUrl: './file-list.html',
  styleUrl: './file-list.css'
})
export class FileList implements OnInit {
  files: FileMetadata[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 5;

  private fileService = inject(FileService);

  ngOnInit() {
    this.loadFiles();
  }

  loadFiles(page: number = 0) {
    this.fileService.list(page, this.pageSize).subscribe({
      next: (data) => {
        this.files = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
      },
      error: (err) => console.error('Error loading files', err),
    });
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages) {
      this.loadFiles(page);
    }
  }
}
