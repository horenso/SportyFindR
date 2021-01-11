import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

import {HeaderComponent} from './components/header/header.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {httpInterceptorProviders} from './interceptors';
import {LocationViewComponent} from './components/location-view/location-view.component';
import {MapComponent} from './components/map/map.component';
import {SpotViewComponent} from './components/spot-view/spot-view.component';
import {MapSidebarComponent} from './components/map-sidebar/map-sidebar.component';
import {CreateNewLocationAndSpotComponent} from './components/create-new-location-and-spot/create-new-location-and-spot.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MessageDatePipe} from './pipes/message-date.pipe';
import {CreateNewSpotComponent} from './components/create-new-spot/create-new-spot.component';
import {SpotFormComponent} from './components/spot-form/spot-form.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {EditSpotComponent} from './components/edit-spot/edit-spot.component';
import {TextWithHashtagsComponent} from './components/text-with-hashtags/text-with-hashtags.component';

import {MaterialModule} from './material/material.module';
import {ToastrModule} from 'ngx-toastr';

const toastrSettings = {
  timeOut: 3000,
  positionClass: 'toast-bottom-center',
  preventDuplicates: true,
};

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    SpotViewComponent,
    MapComponent,
    MapSidebarComponent,
    MessageDatePipe,
    CreateNewLocationAndSpotComponent,
    LocationViewComponent,
    CreateNewSpotComponent,
    SpotFormComponent,
    PageNotFoundComponent,
    EditSpotComponent,
    TextWithHashtagsComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    LeafletModule,
    MaterialModule,
    ToastrModule.forRoot(toastrSettings),
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
