import { Component, OnInit } from '@angular/core';
import {Message} from '../../dtos/message';
import {Spot} from '../../dtos/spot';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MessageService} from '../../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {SpotService} from '../../services/spot.service';

@Component({
  selector: 'app-location-spots',
  templateUrl: './location-spots.component.html',
  styleUrls: ['./location-spots.component.scss']
})
export class LocationSpotsComponent implements OnInit {

  spotList: Array<Spot> = [];
  idString: string;
  currentLocation: number;

  spotForm: FormGroup;

  constructor(
    private spotService: SpotService,
    private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    // this.activatedRoute.params.subscribe( params => {
    //   this.idString = params['id'];
    //   this.currentLocation = +this.idString;
    //   if (Number.isInteger(this.currentLocation)) {
    //     this.spotService.getSpotsByLocation(this.currentLocation).subscribe(
    //       (result) => {
    //         this.spotList = result;
    //         console.log(this.spotList);
    //       }
    //       //  (error) => {
    //       //  TODO: handle error
    //       // });
    //     );
    //   }
    // });

    this.spotForm = this.formBuilder.group({
      name: [null, [Validators.required, Validators.minLength(1)]],
    });
  }

}
