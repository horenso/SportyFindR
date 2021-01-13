import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CreateNewLocationAndSpotComponent} from './components/create-new-location-and-spot/create-new-location-and-spot.component';
import {CreateNewSpotComponent} from './components/create-new-spot/create-new-spot.component';
import {EditSpotComponent} from './components/edit-spot/edit-spot.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {SpotViewComponent} from './components/spot-view/spot-view.component';
import {LocationViewComponent} from './components/location-view/location-view.component';

const routes: Routes = [
  {path: '', component: HomeComponent, children: [
    {path: 'locations/new', component: CreateNewLocationAndSpotComponent},
    {path: 'locations/:locId', component: LocationViewComponent},
    {path: 'locations/:locId/spots/new', component: CreateNewSpotComponent},
    {path: 'locations/:locId/spots/:spotId/edit', component: EditSpotComponent},
    {path: 'locations/:locId/spots/:spotId', component: SpotViewComponent},
  ]},
  {path: 'login', component: LoginComponent},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'legacy'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
