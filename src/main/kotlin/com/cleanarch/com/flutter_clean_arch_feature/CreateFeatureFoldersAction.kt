package com.mhdn.fluttercleanarch

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import java.io.File

class CreateFeatureFoldersAction : AnAction("Create Feature Folders") {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        val clickedDir = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE)
        if (clickedDir == null || !clickedDir.isDirectory) {
            Messages.showErrorDialog("Please right-click on a folder.", "Error")
            return
        }

        val basePath = clickedDir.path

        val featureName = Messages.showInputDialog(
            project,
            "Enter the feature name (e.g., login):",
            "Feature Name",
            null
        )?.takeIf { it.isNotBlank() } ?: return

        val snakeFeature = toSnakeCase(featureName)
        val pascalFeature = toPascalCase(featureName)
        val featurePath = "$basePath/$snakeFeature"

        val folders = listOf(
            "domain/entities",
            "domain/repo",
            "domain/usecases",
            "data/models",
            "data/repo",
            "data/data_sources/remote",
            "presentation/pages",
            "presentation/widgets",
            "presentation/blocs"
        )

        folders.forEach { path ->
            File("$featurePath/$path").mkdirs()
        }

        val files = mapOf(
            "domain/repo/${snakeFeature}_repo.dart" to getRepoFileContent(pascalFeature),
            "data/repo/${snakeFeature}_repo_impl.dart" to getRepoImplContent(pascalFeature, snakeFeature),
            "data/data_sources/remote/${snakeFeature}_remote_data_source.dart" to getRemoteDataSourceContent(pascalFeature),
            "data/data_sources/remote/${snakeFeature}_remote_data_source_impl.dart" to getRemoteDataSourceImplContent(pascalFeature, snakeFeature)
        )

        files.forEach { (path, content) ->
            val file = File("$featurePath/$path")
            if (!file.exists()) {
                file.writeText(content)
            }
        }

        Messages.showInfoMessage(
            "Feature '$snakeFeature' created successfully at:\n$featurePath",
            "Success"
        )
    }


    private fun toSnakeCase(input: String): String {
        return input.replace(Regex("([a-z])([A-Z])"), "$1_$2").replace(" ", "_").lowercase()
    }

    private fun toPascalCase(input: String): String {
        return input.split('_', ' ', '-').joinToString("") { it.capitalize() }
    }

    private fun getRepoFileContent(name: String) = "abstract class ${name}Repo {\n\n}"

    private fun getRepoImplContent(name: String, snake: String) = """
        import 'package:injectable/injectable.dart';
        import '../../domain/repo/${snake}_repo.dart';

        @LazySingleton(as: ${name}Repo)
        class ${name}RepoImpl implements ${name}Repo {
        
        }
    """.trimIndent()

    private fun getRemoteDataSourceContent(name: String) = "abstract class ${name}RemoteDataSource {\n\n}"

    private fun getRemoteDataSourceImplContent(name: String, snake: String) = """
        import 'package:injectable/injectable.dart';
        import '../../data_sources/remote/${snake}_remote_data_source.dart';

        @LazySingleton(as: ${name}RemoteDataSource)
        class ${name}RemoteDataSourceImpl implements ${name}RemoteDataSource {
        
        }
    """.trimIndent()
}
