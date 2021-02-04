import {NgModule} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {MatNativeDateModule, MatOptionModule, MAT_DATE_LOCALE} from '@angular/material/core';
import {MatMenuModule} from '@angular/material/menu';
import {MatCardModule} from '@angular/material/card';
import {MatTabsModule} from '@angular/material/tabs';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatListModule} from '@angular/material/list';
import {MatSliderModule} from '@angular/material/slider';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatTableModule} from '@angular/material/table';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatAutocompleteModule} from '@angular/material/autocomplete';

import {NgxMatDatetimePickerModule, NgxMatNativeDateModule, NgxMatTimepickerModule}
from '@angular-material-components/datetime-picker';
import {MatChipsModule} from '@angular/material/chips';

const MaterialComponents = [
  MatButtonModule,
  MatToolbarModule,
  MatIconModule,
  MatSidenavModule,
  MatFormFieldModule,
  MatInputModule,
  MatSelectModule,
  MatOptionModule,
  MatMenuModule,
  MatCardModule,
  MatTabsModule,
  MatGridListModule,
  MatListModule,
  MatSliderModule,
  MatDatepickerModule,
  MatNativeDateModule,
  MatExpansionModule,
  MatNativeDateModule,
  MatCheckboxModule,
  MatTableModule,
  MatSlideToggleModule,
  MatPaginatorModule,
  MatAutocompleteModule,
  MatChipsModule,

  // Date time picker:
  NgxMatDatetimePickerModule,
  NgxMatTimepickerModule,
  NgxMatNativeDateModule,
];

@NgModule({
  imports: [MaterialComponents],
  exports: [MaterialComponents],
  providers: [{ provide: MAT_DATE_LOCALE, useValue: { useUtc: true } }]
})
export class MaterialModule { }
