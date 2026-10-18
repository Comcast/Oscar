import importlib.util
from pathlib import Path
import subprocess
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location("publisher", Path(__file__).with_name("publish-release.py"))
publisher = importlib.util.module_from_spec(spec)
spec.loader.exec_module(publisher)


class PublishReleaseTest(unittest.TestCase):
    def missing(self):
        return subprocess.CalledProcessError(1, "gh", stderr="gh: Not Found (HTTP 404)")

    @patch.object(publisher, "gh")
    def test_creates_only_after_remote_tag_check(self, gh):
        gh.side_effect = ["Comcast/Oscar", "{}", self.missing(), "release-url"]
        publisher.publish("v3.2.0")
        self.assertEqual(gh.call_args_list[1].args, ("api", "repos/Comcast/Oscar/git/ref/tags/v3.2.0"))
        self.assertEqual(gh.call_args.args, ("release", "create", "v3.2.0", "--repo", "Comcast/Oscar", "--title", "OSCAR v3.2.0", "--generate-notes"))

    @patch.object(publisher, "gh")
    def test_missing_tag_does_not_create(self, gh):
        gh.side_effect = ["Comcast/Oscar", self.missing()]
        with self.assertRaises(subprocess.CalledProcessError):
            publisher.publish("v3.2.0")
        self.assertEqual(gh.call_count, 2)

    @patch.object(publisher, "gh")
    def test_existing_release_is_unchanged(self, gh):
        gh.side_effect = ["Comcast/Oscar", "{}", '{"draft":false,"html_url":"release-url"}']
        publisher.publish("v3.2.0")
        self.assertEqual(gh.call_count, 3)

    @patch.object(publisher, "gh")
    def test_draft_is_not_published(self, gh):
        gh.side_effect = ["Comcast/Oscar", "{}", '{"draft":true}']
        with self.assertRaises(ValueError):
            publisher.publish("v3.2.0")
        self.assertEqual(gh.call_count, 3)

    @patch.object(publisher, "gh")
    def test_dry_run_does_not_publish(self, gh):
        gh.side_effect = ["Comcast/Oscar", "{}", self.missing()]
        publisher.publish("v3.2.0", dry_run=True)
        self.assertEqual(gh.call_count, 3)

    @patch.object(publisher, "gh")
    def test_auth_failure_does_not_create(self, gh):
        gh.side_effect = ["Comcast/Oscar", "{}", subprocess.CalledProcessError(1, "gh", stderr="HTTP 401")]
        with self.assertRaises(subprocess.CalledProcessError):
            publisher.publish("v3.2.0")
        self.assertEqual(gh.call_count, 3)

    @patch.object(publisher, "gh")
    def test_invalid_tag_does_not_contact_github(self, gh):
        with self.assertRaises(ValueError):
            publisher.publish("--bad")
        gh.assert_not_called()
