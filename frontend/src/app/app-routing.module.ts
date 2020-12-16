import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import { SpotMessagesComponent } from './components/spot-messages/spot-messages.component';
import {LocationSpotsComponent} from './components/location-spots/location-spots.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  { path: 'spot',
		children: [
			{ path: ':id', component: SpotMessagesComponent }
		]
	},
  { path: 'location',
    children: [
      { path: ':id', component: LocationSpotsComponent }
    ]
  }

];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
