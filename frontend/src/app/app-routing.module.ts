import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CreateNewLocationAndSpotComponent} from './components/create-new-location-and-spot/create-new-location-and-spot.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {SpotMessagesComponent} from './components/spot-messages/spot-messages.component';
import {ViewSpotsComponent} from './components/view-spots/view-spots.component';

const routes: Routes = [
  {
    path: '', component: HomeComponent, children: [
      {path: 'locations/new', component: CreateNewLocationAndSpotComponent},
      {path: 'locations/:locId', component: ViewSpotsComponent},
      {path: 'locations/:locId/spots/new', component: SpotMessagesComponent},
      {path: 'locations/:locId/spots/:spotId', component: SpotMessagesComponent},
    ]
  },
  {path: 'login', component: LoginComponent},
  {
    path: 'spot',
    children: [
      {path: ':id', component: SpotMessagesComponent}
    ]
  },
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'legacy'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
