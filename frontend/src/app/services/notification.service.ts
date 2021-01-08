import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

	constructor(private toastr: ToastrService) { }

	success(message: string) {
		console.log('Success: ' + message);
		this.toastr.success(message);
	}

	warning(message: string) {
		this.toastr.warning(message);
	}

	error(message: string) {
		this.toastr.error(message);
	}

	info(message: string) {
		this.toastr.info(message);
	}
}
