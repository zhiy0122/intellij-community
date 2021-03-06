// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.plugins.github.pullrequest.config

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@State(name = "GithubPullRequestsUISettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)], reportStatistic = false)
class GithubPullRequestsProjectUISettings : PersistentStateComponentWithModificationTracker<GithubPullRequestsProjectUISettings.SettingsState> {
  private var state: SettingsState = SettingsState()

  class SettingsState : BaseState() {
    var hiddenUrls by stringSet()
    var recentSearchFilters by list<String>()
  }

  fun getHiddenUrls(): Set<String> = state.hiddenUrls.toSet()

  fun addHiddenUrl(url: String) {
    if (state.hiddenUrls.add(url)) {
      state.intIncrementModificationCount()
    }
  }

  fun removeHiddenUrl(url: String) {
    if (state.hiddenUrls.remove(url)) {
      state.intIncrementModificationCount()
    }
  }

  fun getRecentSearchFilters(): List<String> = state.recentSearchFilters.toList()

  fun addRecentSearchFilter(searchFilter: String) {
    val addExisting = state.recentSearchFilters.remove(searchFilter)
    state.recentSearchFilters.add(0, searchFilter)

    if (state.recentSearchFilters.size > RECENT_SEARCH_FILTERS_LIMIT) {
      state.recentSearchFilters.removeLastOrNull()
    }

    if (!addExisting) {
      state.intIncrementModificationCount()
    }
  }

  override fun getStateModificationCount() = state.modificationCount
  override fun getState() = state
  override fun loadState(state: SettingsState) {
    this.state = state
  }

  companion object {
    @JvmStatic
    fun getInstance(project: Project) = project.service<GithubPullRequestsProjectUISettings>()

    private const val RECENT_SEARCH_FILTERS_LIMIT = 10
  }
}
