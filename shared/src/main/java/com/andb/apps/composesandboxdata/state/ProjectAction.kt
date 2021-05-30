package com.andb.apps.composesandboxdata.state

import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.PrototypeTree
import com.andb.apps.composesandboxdata.model.Theme
import kotlinx.serialization.Serializable

@Serializable
sealed class ProjectAction {
    @Serializable data class ExtractComponent(val tree: PrototypeTree, val oldComponents: List<PrototypeComponent>) : ProjectAction()
    @Serializable data class DeleteTree(val tree: PrototypeTree) : ProjectAction()
    @Serializable data class AddTree(val tree: PrototypeTree) : ProjectAction()
    @Serializable data class UpdateTheme(val theme: Theme) : ProjectAction()
    @Serializable data class UpdateName(val name: String) : ProjectAction()
    @Serializable sealed class TreeAction : ProjectAction() {
        @Serializable data class UpdateName(val tree: PrototypeTree, val name: String) : TreeAction()
        @Serializable data class AddComponent(val adding: PrototypeComponent, val parent: PrototypeComponent.Group, val indexInParent: Int) : TreeAction()
        @Serializable data class MoveComponent(val moving: PrototypeComponent, val newParent: PrototypeComponent.Group, val indexInNewParent: Int) : TreeAction()
        @Serializable data class UpdateComponent(val component: PrototypeComponent) : TreeAction()
        @Serializable data class DeleteComponent(val deleting: PrototypeComponent) : TreeAction()
    }
}

data class ActionResult<T>(val value: T, val result: ProjectAction?)