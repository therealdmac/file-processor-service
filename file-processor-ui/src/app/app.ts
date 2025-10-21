import { Component, signal, ViewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FileUpload } from './file-upload/file-upload';
import { FileList } from './file-list/file-list';

@Component({
  selector: 'app-root',
  imports: [FileUpload, FileList, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('file-processor-ui');
  @ViewChild('fileList') fileList!: FileList;

  onUploadCompleted() {
    console.log('📂 Refreshing file list after upload');
    this.fileList.loadFiles(); // reloads from API
  }
}
