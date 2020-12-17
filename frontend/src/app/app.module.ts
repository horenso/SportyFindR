import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { MapComponent } from './components/map/map.component';
import { SpotMessagesComponent } from './components/spot-messages/spot-messages.component';
import { MapSidebarComponent } from './components/map-sidebar/map-sidebar.component';
import { CreateNewLocationAndSpotComponent } from './components/create-new-location-and-spot/create-new-location-and-spot.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LocationSpotsComponent } from './components/location-spots/location-spots.component';
import { ViewSpotsComponent } from './components/view-spots/view-spots.component';
import {MapComponent} from './components/map/map.component';
import {SpotMessagesComponent} from './components/spot-messages/spot-messages.component';
import {MapSidebarComponent} from './components/map-sidebar/map-sidebar.component';
import {CreateNewLocationAndSpotComponent} from './components/create-new-location-and-spot/create-new-location-and-spot.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {MessageDatePipe} from './pipes/message-date.pipe';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    SpotMessagesComponent,
    MapComponent,
    MapSidebarComponent,
    MessageDatePipe,
    CreateNewLocationAndSpotComponent,
    LocationSpotsComponent,
    ViewSpotsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    LeafletModule,
    FontAwesomeModule,
    BrowserAnimationsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
