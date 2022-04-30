!create  car1 : Car
!create rental1 : Rental
!create carGroup1 : CarGroup
!create branch1 : Branch
!create customer1 : Customer

!set car1.registrationNumber := 'AE86'
!set rental1.id := '1'
!set carGroup1.category := 'A'
!set branch1.id := '1'
!set customer1.id := '1'
!set rental1.agreedEnding := 20220427
!set customer1.licenseExpDate := 20220429


!insert (car1,rental1) into Car_Rental
!insert (car1,carGroup1) into Car_CarGroup
!insert (customer1,rental1) into Drivers
!insert (carGroup1, branch1) into CarGroup_Branch