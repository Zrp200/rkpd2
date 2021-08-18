/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.zrp200.rkpd2.services.updates;

//TODO with install and review functionality, this service is less and less just about updates
// perhaps rename to PlatformService, StoreService, DistributionService, etc?
public interface UpdateService {

	interface UpdateResultCallback {
		void onUpdateAvailable( AvailableUpdateData update );
		void onNoUpdateFound();
		void onConnectionFailed();
	}

	//whether the app is updateable via an ingame prompt (e.g. not a demo or an android instant app)
	boolean isUpdateable();

	//whether the service supports an opt-in channel for betas
	boolean supportsBetaChannel();

	void checkForUpdate( boolean useMetered, boolean includeBetas, UpdateResultCallback callback );

	void initializeUpdate( AvailableUpdateData update );

	//whether the app installable via an ingame prompt (e.g. a demo, or an android instant app)
	boolean isInstallable();

	void initializeInstall();

	interface ReviewResultCallback {
		void onComplete();
	}

	boolean supportsReviews();

	void initializeReview(ReviewResultCallback callback);

	void openReviewURI();

}
