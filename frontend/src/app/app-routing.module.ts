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
import {HashtagComponent} from './components/hashtag/hashtag.component';
import {UserManagerComponent} from "./components/user-manager/user-manager.component";

const routes: Routes = [
  {path: '', component: HomeComponent, children: [
    {path: 'locations/new', component: CreateNewLocationAndSpotComponent},
    {path: 'locations/:locId', component: LocationViewComponent},
    {path: 'locations/:locId/spots/new', component: CreateNewSpotComponent},
    {path: 'locations/:locId/spots/:spotId/edit', component: EditSpotComponent},
    {path: 'locations/:locId/spots/:spotId', component: SpotViewComponent},
    {path: 'hashtags/:hashtagName', component: HashtagComponent},
  ]},
  {path: 'login', component: LoginComponent},
  {path: 'user-admin', component: UserManagerComponent},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {relativeLinkResolution: 'legacy'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
