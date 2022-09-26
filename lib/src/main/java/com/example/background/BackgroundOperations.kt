/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import com.example.background.workers.CleanupWorker
import com.example.background.workers.filters.GetAudioFilesWorker

/**
 * Builds and holds WorkContinuation based on supplied filters.
 */
@SuppressLint("EnqueueWork")
class BackgroundOperations(
    context: Context,
    save: Boolean = false
) {

    val continuation: WorkContinuation

    init {
        continuation = WorkManager.getInstance(context)
            .beginUniqueWork(
                Constants.IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanupWorker::class.java)
            ).thenMaybe<GetAudioFilesWorker>(true)
    }

    /**
     * Applies a [ListenableWorker] to a [WorkContinuation] in case [apply] is `true`.
     */
    private inline fun <reified T : ListenableWorker> WorkContinuation.thenMaybe(
        apply: Boolean
    ): WorkContinuation {
        return if (apply) {
            then(workRequest<T>())
        } else {
            this
        }
    }

    /**
     * Creates a [OneTimeWorkRequest] with the given inputData and a [tag] if set.
     */
    private inline fun <reified T : ListenableWorker> workRequest(
        // inputData: Data = imageInputData,
        tag: String? = null
    ) =
        OneTimeWorkRequestBuilder<T>().apply {
            // setInputData(inputData)
            setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            if (!tag.isNullOrEmpty()) {
                addTag(tag)
            }
        }.build()
}
